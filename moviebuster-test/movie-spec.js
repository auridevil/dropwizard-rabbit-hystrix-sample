/* eslint-env jasmine */

'use strict';

var request = require('request');
var ASQ = require('asynquence-contrib');
var apigw = {
    host: process.env.APIGW || 'http://localhost',
    port: process.env.APIGW_PORT || '8080'
};

describe('movie-ms handler', function() {

var movie = {
    'title': 'From dusk til dawn',
    'movieType': 'OLD'
};

var movie_from_migr = {
    id: 1,
    title: "Lock'n'Stock",
    movieType: "REGULAR"
};


it('should insert a movie', function(done) {
    var host = apigw.host + ':' + apigw.port + '/movie';
    request.post(
        {
            uri: host,
            body: movie,
            json: true,
            headers: {
                'Authorization': 'bearer ' + process.env.APIGW_KEY
            }
        }, function(error, response, body) {
            expect(error).toBeNull();
            expect(response).toBeDefined();
            expect(response.statusCode).toEqual(201);
            expect(body).toBeDefined();
            expect(body.id).toBeDefined();
            expect(body.title).toEqual(movie.title);
            expect(body.movieType).toEqual(movie.movieType);
            done();
        });
});

it('should get a movie', function(done) {
    var host = apigw.host + ':' + apigw.port + '/movie/' + movie_from_migr.id;
    request.get(
        {
            uri: host,
            json: true,
            headers: {
                'Authorization': 'bearer ' + process.env.APIGW_KEY
            }
        }, function(error, response, body) {
            expect(error).toBeNull();
            expect(response).toBeDefined();
            expect(response.statusCode).toEqual(200);
            expect(body).toBeDefined();
            expect(body.id).toBeDefined();
            expect(body.title).toEqual(movie_from_migr.title);
            expect(body.movieType).toEqual(movie_from_migr.movieType);
            done();
        });
});

it('should get all movies', function(done) {
    var host = apigw.host + ':' + apigw.port + '/movie';
    request.get(
        {
            uri: host,
            json: true,
            headers: {
                'Authorization': 'bearer ' + process.env.APIGW_KEY
            }
        }, function(error, response, body) {
            expect(error).toBeNull();
            expect(response).toBeDefined();
            expect(response.statusCode).toEqual(200);
            expect(body).toBeDefined();
            expect(body.length).toBeDefined();
            expect(body[0].id).toBeDefined();
            expect(body[0].title).toEqual(movie_from_migr.title);
            expect(body[0].movieType).toEqual(movie_from_migr.movieType);
            done();
        });
});

it('should update a movie', function(done) {
    var host = apigw.host + ':' + apigw.port + '/movie';
    var createdMovie;
    ASQ(function create(stepdone) {
        request.post(
            {
                uri: host,
                body: movie,
                json: true,
                headers: {
                    'Authorization': 'bearer ' + process.env.APIGW_KEY
                }
            }, function(error, response, body) {
                expect(error).toBeNull();
                expect(response).toBeDefined();
                expect(response.statusCode).toEqual(201);
                expect(body).toBeDefined();
                createdMovie = body;
                stepdone();
            });
    }).then(function update(stepdone) {
        createdMovie.title = 'updated title';
        request.put(
            {
                uri: host + '/' + createdMovie.id,
                body: {
                    title: createdMovie.title
                },
                json: true,
                headers: {
                    'Authorization': 'bearer ' + process.env.APIGW_KEY
                }
            }, function(error, response, body) {
                expect(error).toBeNull();
                expect(response).toBeDefined();
                expect(response.statusCode).toEqual(200);
                expect(body).toBeDefined();
                expect(body.id).toEqual(createdMovie.id);
                expect(body.title).toEqual(createdMovie.title);
                expect(body.movieType).toEqual(createdMovie.movieType);
                stepdone();
            });
    }).then(function complete(stepdone) {
        done();
    }
    );
});

it('should delete a movie', function(done) {
    var host = apigw.host + ':' + apigw.port + '/movie';
    var createdMovie;
    ASQ(function create(stepdone) {
        request.post(
            {
                uri: host,
                body: movie,
                json: true,
                headers: {
                    'Authorization': 'bearer ' + process.env.APIGW_KEY
                }
            }, function(error, response, body) {
                expect(error).toBeNull();
                expect(response).toBeDefined();
                expect(response.statusCode).toEqual(201);
                expect(body).toBeDefined();
                createdMovie = body;
                stepdone();
            });
    }).then(function del(stepdone) {
        request.delete(
            {
                uri: host + '/' + createdMovie.id,
                json: true,
                headers: {
                    'Authorization': 'bearer ' + process.env.APIGW_KEY
                }
            }, function(error, response, body) {
                expect(error).toBeNull();
                expect(response).toBeDefined();
                expect(response.statusCode).toEqual(200);
                expect(body).toBeDefined();
                expect(body.id).toEqual(createdMovie.id);
                expect(body.title).toEqual(createdMovie.title);
                expect(body.movieType).toEqual(createdMovie.movieType);
                stepdone();
            });
    }).then(function verify(stepdone) {
        request.get(
            {
                uri: host + '/' + createdMovie.id,
                json: true,
                headers: {
                    'Authorization': 'bearer ' + process.env.APIGW_KEY
                }
            }, function(error, response, body) {
                expect(error).toBeNull();
                expect(response).toBeDefined();
                expect(response.statusCode).toEqual(204);
                expect(body).toBeUndefined();
                stepdone();
            });
    }).then(function complete(stepdone) {
        done();
    });
});

});
