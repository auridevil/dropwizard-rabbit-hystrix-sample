/* eslint-env jasmine */

'use strict';

var request = require('request');
var apigw = {
    host: process.env.APIGW || 'http://localhost',
    port: process.env.APIGW_PORT || '8080'
};

describe('bonus-ms handler', function() {

var bonusRequest = {
    'customerId': 5,
    'bonusQuantity': 2
};


it('should insert a bonus', function(done) {
    var host = apigw.host + ':' + apigw.port + '/bonus';
    request.post(
        {
            uri: host,
            body: bonusRequest,
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
            expect(body.customerId).toEqual(bonusRequest.customerId);
            expect(body.bonusQuantity).toEqual(bonusRequest.bonusQuantity);
            done();
        });
});

});
