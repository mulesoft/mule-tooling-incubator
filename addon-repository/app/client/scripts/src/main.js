/* global document, window */
'use strict';

var Template = require('./template'),
	repository = require('./repository'),
	MuleStudio = require('./studio'),

	studio = new MuleStudio(window),
	addOnItemTemplate = new Template(document.getElementById('addon-item-template')),
	addOnList = document.getElementById('addon-list');


function installAddOn(button) {
	var id = button.dataset.id,
		version = button.dataset.version,
		parent = button.parentNode,
		progressBar = parent.querySelector('.progress-bar');

	parent.classList.add('install-in-progress');
	progressBar.style.setProperty('width', 0);

	return studio.install(id, version).
		progress(function (data) {
			progressBar.style.setProperty('width', Math.max(parseInt(data), 0) + '%');
		}).
		then(function () {
			parent.classList.add('installed');
		}).
		fin(function () {
			parent.classList.remove('install-in-progress');
		});
}

function showAddOns(addOns) {
	addOnList.innerHTML = '';

	addOns.forEach(function (addOn) {
		addOnList.appendChild(addOnItemTemplate.createElement(addOn));
	});
}

function showLoadAddOnsError(err) {
	console.log(err);
}

function loadAddOnsList() {
	repository.findAddOns().
		then(showAddOns).
		fail(showLoadAddOnsError);
}

addOnList.addEventListener('click', function (evt) {
	if (evt.target.classList.contains('btn-install')) {
		installAddOn(evt.target);
	}
}, true);

window.notifyStudioListener = studio.emit.bind(studio);

loadAddOnsList();