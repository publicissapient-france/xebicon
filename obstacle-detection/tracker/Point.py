class Point:
    """ This class handles pixel position """

    def __init__(self, x, y):
        self.x = x
        self.y = y

    def __repr__(self):
        'Return a nicely formatted representation string'
        return 'Point(x=%r, y=%r)' % self
