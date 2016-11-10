from tracker import Window
from tracker import Point
import sys, getopt
import argparse
import KeyNoteStatusListener

# launcher props based on first rapberry video
def parse_args():
    """
    Parse all arguments passed to this program
    """
    parser = argparse.ArgumentParser(
        description="Play the animal tracker"
    )

    parser.add_argument(
        '--host', action="store", dest="host", default=None,
        help="Hostname/IP of the pi camera streaming"
    )

    parser.add_argument(
        '--visualize', action="store", dest="visualize", default=True,
        help="visualize camera streaming image"
    )

    args = parser.parse_args()

    if not (args.host):
        parser.error("Host is missing.")
        exit(1)
    args.visualize = (args.visualize) in ('True', '1')
    return args


def main(argv):
    args = parse_args()
    w = 200
    h = 200
    p1 = Point(160, 37)
    p2 = Point(160 + w, 37 + h)
    Window('http://' + args.host + '/html/cam_pic_new.php', p1, p2,args.visualize)


if __name__ == "__main__":
    main(sys.argv[1:])
