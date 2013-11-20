'use strict';

var newClass = require('barman').Class.create;


var Template = newClass({

	constructor: function (protoElement) {
		this._el = protoElement.cloneNode(true);
	},

	_clone: function () {
		var el = this._el.cloneNode(true);
		el.classList.remove('template');
		el.removeAttribute('id');
		return el;
	},

	createElement: function (data) {
		return this._replacePlaceholders(this._clone(), data);
	},

	_replacePlaceholders: function (el, data) {
		var children = el.children;

		for (var i = 0; i < children.length; i++) {
			this._replaceElementPlaceholder(children[i], data);
		}

		return el;
	},

	_camelCaseToAttribute: function (dataKey) {
		return dataKey.substring('attr'.length).replace(/([a-z])([A-Z])/g, '$1-$2').toLowerCase();
	},

	_replaceElementPlaceholder: function (el, data) {
		this._replacePlaceholders(el, data);

		Object.keys(el.dataset).forEach(function (dataKey) {
			var field = el.dataset[dataKey];

			if (dataKey.indexOf('attr') === 0) {

				el.setAttribute(this._camelCaseToAttribute(dataKey), data[field]);

			} else if (dataKey === 'contents') {

				el.textContent = data[field];
			}

		}.bind(this));
	}
});


module.exports = Template;