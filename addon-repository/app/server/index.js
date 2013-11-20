'use strict';

var express = require('express'),
	app = express(),

	path = require('path'),

	port = 4001,
	baseUrl = 'http://localhost:' + port,

	MockRepository = require('../../lib/mock-repository'),
	repository = new MockRepository(require('./mock-addon-list.json'));


app.use(express.static(path.resolve(__dirname, '../client')));

app.get('/api/addons', function (req, res, next) {

	repository.findAddOns(req.query.q).
		then(function (result) {
			res.json(result);
		}).
		fail(next);

});

app.get('/api/addons/:id', function (req, res, next) {

	repository.findAddOn(res.params.id).
		then(function (result) {
			res.json(result);
		}).
		fail(next);

});


app.listen(port);
console.log('Running on: ' + baseUrl);