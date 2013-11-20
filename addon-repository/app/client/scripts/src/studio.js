'use strict';

var newClass = require('barman').Class.create,
	EventEmitter = require('events'),
	q = require('q');


var MuleStudio = newClass({

	constructor: function (window) {
		this._msgSequence = 1;
		this._emitter = new EventEmitter();
		this._window = window;
		this._callbacks = [];
	},

	sendMessage: function (message) {
		var id = this._newMessageId(),
			defer = q.defer(),
			progressEvent = 'progress:' + id,
			emitter = this._emitter;

		emitter.on(progressEvent, function (data) { defer.notify(data); });

		emitter.once('callback:' + id, function (data) {
			emitter.removeAllListeners(progressEvent);
			defer.resolve(data);
		});

		// Studio mock
		var n = 0;
		var w = this._window;
		var progress = function () {
			console.log('PROGRESS!!!!');
			n = n + 10;
			emitter.emit('progress:'+id, n);
			if (n < 100) {
				w.setTimeout(progress, 1000);
			} else {
				emitter.emit('callback:'+id);
			}
		};
		w.setTimeout(progress, 1000);
		// --- Studio mock

		this._window.status = this._serializeMessage(id, message);

		return defer.promise;
	},

	_serializeMessage: function (id, message) {
		return "message:" + id + ":" + message;
	},

	_newMessageId: function () {
		return this._msgSequence++;
	},

	on: function (event, listener) {
		this._emitter.on(event, listener);
		return this;
	},

	off: function (event, listener) {
		this._emitter.removeListener(event, listener);
		return this;
	},

	removeAllListeners: function (event) {
		this._emitter.removeAllListeners(event);
		return this;
	},

	emit: function (event, message) {
		return this._emitter.emit(event, message);
	},

	install: function (id, version) {
		return this.sendMessage('install->' + id + ',' + version);
	}

});

module.exports = MuleStudio;