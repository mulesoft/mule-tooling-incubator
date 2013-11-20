'use strict';

var barman = require('barman'),
	newClass = barman.Class.create,
	required = barman.required;


module.exports = newClass({

	_q: require('q'),

	findAddOns: required,

	findAddOn: function (id) {
		return this.findAddOns({id: id}).then(function (result) {
			return result[0];
		});
	}

});