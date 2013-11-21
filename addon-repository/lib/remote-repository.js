'use strict';

var AbstractRepository = require('./repository');


module.exports = AbstractRepository.extend({

	constructor: function (http, baseUrl) {
		this._http = http;
		this._baseUrl = baseUrl || '';
	},

	findAddOns: function (query) {
		return this._getJSON('/addons', {q: query});
	},

	_getJSON: function (path, parameters) {
		var config = {};
		if (parameters) {
			config.params = parameters;
		}
		return this._http.get(this._apiUrl(path), config).then(function (result) {
			return result.data;
		});
	},

	_apiUrl: function (path) {
		return this._baseUrl + '/api' + path;
	}

});