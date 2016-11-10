from tracker import Window
from tracker import Point
from array import array
import sys, getopt


# launcher props based on first rapberry video
def main(argv):
    # TODO: read the url as an input parameter
    w = 200
    h = 200
    p1 = Point(160, 37)
    p2 = Point(160 + w, 37 + h)
    Window('http://10.7.13.97/html/cam_pic_new.php?pDelay=40000', p1, p2)


if __name__ == "__main__":
    main(sys.argv[1:])
