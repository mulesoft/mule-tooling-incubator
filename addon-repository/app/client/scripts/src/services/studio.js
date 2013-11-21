'use strict';

var EventEmitter = require('events');


function studioServiceFactory($window, $q) {

	var msgSequence = 1,
		emitter = new EventEmitter(),
		callbacks = [];

	var studio = {
		sendMessage: function (message) {
			var id = msgSequence++,
				defer = $q.defer(),
				progressEvent = 'progress:' + id;

			emitter.on(progressEvent, function (data) { defer.notify(data); });

			emitter.once('callback:' + id, function (data) {
				emitter.removeAllListeners(progressEvent);
				defer.resolve(data);
			});

			// Remove for the real implementation
			this._mockStudioProgress(id);
			// ----------------------------------

			$window.status = "message:" + id + ":" + message;

			return defer.promise;
		},

		install: function (id, version) {
			return this.sendMessage('install->' + id + ',' + version);
		},

		installProgress: function (id, n) {
			emitter.emit('progress:' + id, n);
		},

		installFinished: function (id) {
			emitter.emit('callback:' + id);
		},

		// Remove this method for the real implementation
		_mockStudioProgress: function (id) {
			var n = 0, self = this;

			var progress = function () {
				console.log('PROGRESS!!!!');
				n = n + 10;
				self.installProgress(id, n);

				if (n < 100) {
					$window.setTimeout(progress, 1000);
				} else {
					this.installFinished(id);
				}
			};
			$window.setTimeout(progress, 1000);
		}
	};


	$window.installProgress = studio.installProgress.bind(studio);
	$window.installFinished = studio.installFinished.bind(studio);
	// window.notifyStudioListener = studio.emit.bind(studio);

	return studio;
}
studioServiceFactory.$inject = ['$window', '$q'];


module.exports = studioServiceFactory;