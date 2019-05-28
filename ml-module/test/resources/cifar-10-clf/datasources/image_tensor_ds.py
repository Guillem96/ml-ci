import numpy as np
from PIL import Image

import driftai as dai


class ImageTensorDatasource(dai.data.DirectoryDatasource):
    def __init__(self, path, parsing_pattern="{}_{class}.png"):
        super(ImageTensorDatasource, self).__init__(path=path, 
                                                    parsing_pattern=parsing_pattern)

    def loader(self, idx):
        return np.asarray(Image.open(idx)).reshape(32, 32, 3)