'use strict';

var AbstractRepository = require('./repository');


module.exports = AbstractRepository.extend({

	constructor: function (requestImpl, baseUrl) {
		this._request = requestImpl;
		this._baseUrl = baseUrl || '';
	},

	findAddOns: function (query) {
		return this._getJSON('/addons', {q: query});
	},

	_getJSON: function (path, parameters) {
		return this._q.nfcall(this._request,
			{
				url: this._apiUrl(path),
				json: true,
				qs: parameters

			}).spread(function (req, body) {
				return body;
			});
	},

	_apiUrl: function (path) {
		return this._baseUrl + '/api' + path;
	}

});