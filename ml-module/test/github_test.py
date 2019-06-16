import sys
sys.path.append('.')

from ml_ci import entrypoint

entrypoint.train(-1, 'https://github.com/Guillem96/housing-project')