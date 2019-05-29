#!/usr/bin/env python

import os
import subprocess


def delete_dir(path):
    """Remove the specified directory
    """
    for root, dirs, files in os.walk(path, topdown=False):
        for name in files:
            os.chmod(os.path.join(root, name), 0o777)
            os.remove(os.path.join(root, name))
        for name in dirs:
            os.rmdir(os.path.join(root, name))


def execute(command, at):
    """
    Executes a command at the given directory

    Parameters
    ----------
    command: str
        Command to be executed
    at: str
        Current Working Directory of the commnad. (Where the command should be executed)
    
    Returns
    -------
    tuple (str, str)
        stdout, stderr
    """
    p = subprocess.Popen(command.split(), 
                         cwd=at,
                         stdout=subprocess.PIPE, 
                         stderr=subprocess.PIPE)

    stdout, stderr = p.communicate()
    return stdout.decode(), stderr.decode()
