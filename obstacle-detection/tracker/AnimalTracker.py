import logging
import threading

import coloredlogs
import cv2

from prediction.label_image import LabelImage

logger = logging.getLogger("AnimalTracker")
logger.setLevel(logging.INFO)
coloredlogs.install(level='INFO')
coloredlogs.set_level("INFO")


class AnimalTracker:
    """ This class handles the animal detection and form recognition """

    def __init__(self, window_name, debug, visualize, rabbitUser, rabbitPwd):
        logger.info("created")
        self.window_name = window_name
        self.counter = 0
        # images to show
        self.base_img = None
        self.frame = None
        self.diff = None
        self.mask = None
        self.result = None
        self.bbox = None
        self._clear_folder("ML")
        self.valid_images = 0
        self.statusObstacleDetected = False
        self.visualize = visualize
        self.thread = None
        # model for prediction
        self.labelImage = LabelImage('ML',rabbitUser,rabbitPwd)

    def init_if_needed(self, base_img):
        """ This method sets the image used for compare the movement if is not
        already set
        """
        if self.base_img is None:
            self.counter += 1
            logger.info("init_if_needed")
            self.base_img = base_img

    def force_init(self, base_img):
        """ This method overrides the current image used for comparison when
        verifying movement
        """
        self.counter = 0
        logger.info("force_init")
        self.base_img = base_img
        self.statusObstacleDetected = False

    def update(self, curr_frame, threshold_value):
        """ This method updates current behaviour after a new frame is captured
        :param curr_frame: Current frame
        :param threshold_value: The absolute difference between images is a new
        image having only white pixels representing the difference. This
        threshold used to determine if the movement (between is two images) is
        important enough and not simply random noise.
        When the visualize flag is activated a red box around the AOI is shown.
        The animal prediction is executed in a another thread with the last 10
        images as this could be a heavy calculation.
        """
        self.counter += 1
        self.frame = curr_frame

        # calculates diff
        base_blurred = self._blur_and_grey(self.base_img)

        frame_blurred = self._blur_and_grey(curr_frame)
        self.diff = cv2.absdiff(base_blurred, frame_blurred)
        # update references
        _, thresh = cv2.threshold(self.diff, threshold_value, 255, cv2.THRESH_BINARY)
        non_zero = cv2.countNonZero(thresh)

        if self.counter % 100 == 0:
            self._persist_image("stream/",self.diff, "stream")

        if non_zero > 60:
            self.mask = cv2.dilate(thresh, None, iterations=8)
            # transform mask into image
            new_mask = cv2.merge((self.mask, self.mask, self.mask))
            self.result = cv2.bitwise_and(self.frame, new_mask)

            # detecting bounding box
            contours = cv2.findContours(self.mask, 1, 2)
            cnt = contours[0]
            x, y, w, h = cv2.boundingRect(cnt)
            try:
                self.bbox = self.result[y:y + h, x:x + w]
            except cv2.error as e:
                logger.info("Impossible to calculate bbox")

            # ML communication
            if self.counter % 20 == 0 and not self.labelImage.is_processing():
                self.valid_images += 1
                self._persist_iteration(curr_frame, self.counter)

            if self.valid_images > 10:
                self.valid_images = 0
                self.statusObstacleDetected = True
                if self.thread is None or (self.thread is not None and self.thread.isAlive() == False):
                    logger.info("sending image")
                    self.thread = threading.Thread(name='prediction', target=self.prediction)
                    self.thread.start()
        else:
            if self.statusObstacleDetected:
                logger.info("sending clear")
                self.valid_images = 0
                self.statusObstacleDetected = False
                self.send_clear()

    def display(self):
        self._display_if_valid("segmentation", self.result)

    def prediction(self):
        """ This method triggers animal detection """
        self.labelImage.run_inference_on_image()

    def send_clear(self):
        """ This method informs that the obstacle has been removed """
        self.labelImage.send_clear()

    @staticmethod
    def _persist_iteration(img, suffix):
        cv2.imwrite("ML/result-" + str(suffix) + ".jpeg", img)

    @staticmethod
    def _persist_image(folder, img, suffix):
        cv2.imwrite(folder + str(suffix) + ".jpeg", img)

    @staticmethod
    def _blur_and_grey(img):
        """ Blurs and transform to grey level """
        return cv2.cvtColor(cv2.blur(img, (5, 5)), cv2.COLOR_BGR2GRAY)

    @staticmethod
    def _clear_folder(folder_name):
        """ This method clear the files present in the folder specified"""
        import os
        for the_file in os.listdir(folder_name):
            file_path = os.path.join(folder_name, the_file)
            try:
                if os.path.isfile(file_path):
                    os.unlink(file_path)
            except Exception as e:
                logger.info(e)

    @staticmethod
    def _display_if_valid(title, img):
        try:
            if img is not None:
                cv2.imshow(title, img)
        except cv2.error as e:
            logger.info("Impossible to open window")
