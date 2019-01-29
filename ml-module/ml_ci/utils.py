#!/usr/bin/env python

import os

def delete_dir(path):
    """Remove the specified directory
    """
    for root, dirs, files in os.walk(path, topdown=False):
        for name in files:
            os.chmod(os.path.join(root, name), 0o777)
            os.remove(os.path.join(root, name))
        for name in dirs:
            os.rmdir(os.path.join(root, name))
        