import optapp as opt

from sklearn.linear_model import LogisticRegression

@opt.run.single_run
class LogisticRegressionApproach(opt.RunnableApproach):

    @property
    def parameters(self):
        return [
            opt.parameters.FloatParameter('tol', 1e-4, 1e-3, 5),
            opt.parameters.BoolParameter('fit_intercept')
        ]

    def learn(self, data, parameters):
        return LogisticRegression(**parameters).fit(**data)

    def inference(self, model, data):
        return model.predict(data['X'])