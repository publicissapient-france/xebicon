import logging
import urllib

import coloredlogs
import cv2
import numpy as np

import tracker.AnimalTracker
import tracker.Point

logger = logging.getLogger("Window")
logger.setLevel(logging.INFO)
coloredlogs.install(level='INFO')
coloredlogs.set_level("INFO")


class Window:
    """ This class handles the application loop """

    def __init__(self, video_name, p1, p2, visualize):
        logger.info("window of prediction created")
        self.visualize = visualize
        self.p1 = p1
        self.p2 = p2
        self.video_name = video_name

    def init_prediction(self,rabbitUser,rabbitPwd):
        """ This method initializes all objects needed to detect animals.
        AnimalTracker handles mouvement detection
        AOITracker handles the Area Of Interest (AOI), which is basically a small
        portion of the image. This technique allow us to improve performance by
        analysing only a small portion of the image.
        """
        logger.info("init prediction")
        self.predicting = True
        self.window_name = 'image'
        if self.visualize:
            cv2.namedWindow(self.window_name)
        # animal tracker
        self.tracker = tracker.AnimalTracker(self.window_name, False, self.visualize,rabbitUser,rabbitPwd)
        # aoi tracker
        self.aoi = tracker.AOITracker(self.window_name, self.p1, self.p2, self.visualize)
        self.bytes = ''
        # display loop
        self.display_loop(True, self.video_name, self.visualize)
        logger.info("bye")

    def get_streamed_image(self, stream):
        """ This method consumes and cummulates the remote stream """
        self.bytes += stream.read(2048)
        a = self.bytes.find('\xff\xd8')
        b = self.bytes.find('\xff\xd9')
        if a != -1 and b != -1:
            jpg = self.bytes[a:b + 2]
            self.bytes = self.bytes[b + 2:]
            i = cv2.imdecode(np.fromstring(jpg, dtype=np.uint8), cv2.COLOR_BGR2HSV)
            return i

    def stop_prediction(self):
        """ This method stops the processing loop """
        self.predicting = False

    def display_loop(self, condition, video_name, visualize):
        """ This method handles each iteration of the processing loop
        until self.predicting returns False
        """
        logger.info("displayLoop")
        stream = urllib.urlopen(video_name)
        logger.info("video opened")

        while self.predicting:
            frame = self.get_streamed_image(stream)

            # read current frame and resize
            if frame is not None:
                # get info from capture
                height, width = frame.shape[:2]

                # create trackers
                self.aoi.create_trackers_if_needed(width, height)

                # handles AOI
                aoi_img = self.aoi.crop_region(frame.copy())
                self.tracker.init_if_needed(aoi_img)
                self.tracker.update(aoi_img, self.aoi.get_threshold())

                # display images
                self.aoi.display_rectangle(frame)
                if visualize:
                    self.tracker.display()
                    cv2.imshow(self.window_name, frame)
        # release resources
        cv2.destroyAllWindows()
