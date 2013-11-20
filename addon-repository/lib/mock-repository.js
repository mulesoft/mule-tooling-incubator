'use strict';

var AbstractRepository = require('./repository');


module.exports = AbstractRepository.extend({

	constructor: function (addOns) {
		this._addOns = addOns;
	},

	findAddOns: function (query) {
		return this._q(this._addOns);
	}

});