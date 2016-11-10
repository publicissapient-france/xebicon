import cv2
import logging
import coloredlogs

logger = logging.getLogger("AOITracker")
logger.setLevel(logging.INFO)
coloredlogs.install(level='INFO')
coloredlogs.set_level("INFO")

class AOITracker:
    """ Handle tracker components """

    def __init__(self, window_name, p1, p2, visualize):
        logger.info("created")
        self.window_name = window_name
        # initial values
        self.y = p1.y
        self.x = p1.x
        self.max_width = 512
        self.max_height = 288
        self.width = 200
        self.height = 200
        self.threshold = 15
        self.need_refresh = True
        self.visualize = visualize

    def create_trackers_if_needed(self, width, height):
        if self.need_refresh and self.visualize:
            cv2.createTrackbar('posx', self.window_name, self.x, self.max_width, self.nothing)
            cv2.createTrackbar('posy', self.window_name, self.y, self.max_height, self.nothing)
            cv2.createTrackbar('width', self.window_name, width, self.width, self.handle_track_width_change)
            cv2.createTrackbar('height', self.window_name, height, self.height, self.nothing)
            cv2.createTrackbar('threshold', self.window_name, self.threshold, 255, self.handle_track_threshold_change)
        self.need_refresh = False

    def get_threshold(self):
        if self.visualize:
            return cv2.getTrackbarPos('threshold', self.window_name)
        return self.threshold

    def crop_region(self, img):
        (x1, y1, x2, y2) = self._get_current_position()
        return img[y1:y2, x1:x2]

    def display_rectangle(self, img):
        default_color = (0, 0, 255)
        (x1, y1, x2, y2) = self._get_current_position()
        cv2.rectangle(img, (x1, y1), (x2, y2), default_color, 0)

    def _track_positions(self):
        if self.visualize:
            x = cv2.getTrackbarPos('posx', self.window_name)
            y = cv2.getTrackbarPos('posy', self.window_name)
            width = cv2.getTrackbarPos('width', self.window_name)
            height = cv2.getTrackbarPos('height', self.window_name)
            return x, y, width, height
        else:
            return self.x, self.y, self.width, self.height

    def _get_current_position(self):
        (x, y, width, height) = self._track_positions()
        x2 = x + width if x + width < self.max_width else self.max_width
        y2 = y + height if y + height < self.max_height else self.max_height
        return x, y, x2, y2

    def handle_track_threshold_change(self, value):
        self.threshold = value

    def handle_track_width_change(self, value):
        self.width = value

    def nothing(self, param):
        pass
