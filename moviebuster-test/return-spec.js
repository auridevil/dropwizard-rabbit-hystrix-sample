/* eslint-env jasmine */
'use strict';

var request = require('request');
var ASQ = require('asynquence-contrib');
var moment = require('moment');
var apigw = {
    host: process.env.APIGW || 'http://localhost',
    port: process.env.APIGW_PORT || '8080'
};

describe('rent-return-ms handler', function() {

var movies = [
    {
        'title': 'Matrix',
        'movieType': 'NEW'
    },
    {
        'title': 'Spider Man',
        'movieType': 'REGULAR'
    },
    {
        'title': 'Spider Man 2',
        'movieType': 'REGULAR'
    },
    {
        'title': 'Out of Africa',
        'movieType': 'OLD'
    }];

var movieMap = new Map();
var movieArray = [];

beforeEach(function(done) {
    ASQ(function start(stepdone) {
        stepdone(movies);
    }).map(function insert(movie, stepdone) {
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
                movie.id = body.id;
                movieMap.set(body.title, body.id);
                movieArray.push(body.id);
                stepdone();
            });
    }).then(function complete(stepdone) {
        done();
        stepdone();
    }).or(function manage(err) {
        done(err);
    });
});

afterEach(function(done) {
    ASQ(function start(stepdone) {
        stepdone(movieArray);
    }).map(function insert(movieId, stepdone) {
        if (movieId) {
            var host = apigw.host + ':' + apigw.port + '/movie/' + movieId;
            request.delete(
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
                    stepdone();
                });
        } else {
            stepdone();
        }
    }).then(function complete(stepdone) {
        done();
        stepdone();
    }).or(function manage(err) {
        done(err);
    });
});


// Matrix 11 (New release) 2 extra days 80 SEK 
// Spider Man (Regular rental) 1 days 30 SEK T
// Total late charge: 110 SEK 

it('should insert returns', function(done) {

    console.log(moment.utc().format('YYYY-MM-DD-hh:mm'));

    var requestObj = [
        {
            'movieId': movieMap.get('Matrix'),
            'customerId': 10,
            'rentDays': 1,
            'movementDate': moment.utc().subtract(3, 'days').format('YYYY-MM-DD') // fake data to accomplish requirement
        },
        {
            'movieId': movieMap.get('Spider Man'),
            'customerId': 10,
            'rentDays': 5,
            'movementDate': moment.utc().subtract(6, 'days').format('YYYY-MM-DD') // fake data to accomplish requirement
        },
        {
            'movieId': movieMap.get('Spider Man 2'),
            'customerId': 10,
            'rentDays': 2,
            'movementDate': moment.utc().format('YYYY-MM-DD')
        },
        {
            'movieId': movieMap.get('Out of Africa'),
            'customerId': 10,
            'rentDays': 7,
            'movementDate': moment.utc().format('YYYY-MM-DD')

        }];

    var returnList = [];

    ASQ(function insertRent(stepdone) {
        var host = apigw.host + ':' + apigw.port + '/rent/new';
        console.log('Request (before return) request ', requestObj);
        request.post(
            {
                uri: host,
                body: requestObj,
                json: true,
                headers: {
                    'Authorization': 'bearer ' + process.env.APIGW_KEY
                }
            }, function(error, response, body) {
                console.log('Request (before return) response ', body);
                expect(error).toBeNull();
                expect(response).toBeDefined();
                expect(response.statusCode).toEqual(200);
                expect(body).toBeDefined();
                expect(body.priceLines).toBeDefined();
                expect(body.priceLines.length).toEqual(4);
                expect(body.totalPrice).toEqual(250);
                expect(body.totalBonus).toEqual(5);
                for (var i = 0; i < body.priceLines.length; i++) {
                    returnList.push({
                        rentId: body.priceLines[i].rentId
                    });
                }
                stepdone();
            });

    }).then(function insertReturn(stepdone) {
        var host = apigw.host + ':' + apigw.port + '/rent/return';
        request.post(
            {
                uri: host,
                body: returnList,
                json: true,
                headers: {
                    'Authorization': 'bearer ' + process.env.APIGW_KEY
                }
            }, function(error, response, body) {
                console.log('Return response ', body);
                expect(error).toBeNull();
                expect(response).toBeDefined();
                expect(response.statusCode).toEqual(200);
                expect(body).toBeDefined();
                expect(body.priceLines).toBeDefined();
                expect(body.totalPrice).toEqual(110);
                stepdone();
            });

    }).then(function complete(stepdone) {
        done();
        stepdone();
    }).or(function manage(error) {
        done(error);
    });
});


});
