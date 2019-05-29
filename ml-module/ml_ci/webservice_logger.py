from logging import Handler

class RequestsHandler(Handler):
    def __init__(self, webservice, *args, **kwargs):
        super(RequestsHandler, self).__init__(*args, **kwargs)
        self.webservice = webservice

    def emit(self, record):
        log_entry = self.format(record)
        return self.webservice and self.webservice.log(record.levelname, log_entry)
