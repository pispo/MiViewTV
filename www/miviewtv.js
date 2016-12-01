/*global cordova, module*/

var utils = require('cordova/utils'),
  exec = require('cordova/exec'),
  cordova = require('cordova');

function MiViewTV (ipList) {
  this.results = null;
}

MiViewTV.prototype.channels = function (success, err) {
  var successCallback, errorCallback, self;
  
  self = this;
  
  successCallback = function (r) {
    self.results = r;
    if (success && typeof success === 'function') {
      success(r);
    }
  };
  
  errorCallback = function (e) {
    utils.alert('[ERROR] Error initializing Cordova: ' + e);
    if (err && typeof err === 'function') {
      err(e);
    }
  };
  
  exec(successCallback, errorCallback, "MiViewTVPlugin", "getChannels", []);
};

MiViewTV.prototype.programGuide = function (day, success, err) {
  var successCallback, errorCallback, self;
  
  self = this;
  
  successCallback = function (r) {
    self.results = r;
    if (success && typeof success === 'function') {
      success(r);
    }
  };
  
  errorCallback = function (e) {
    utils.alert('[ERROR] Error initializing Cordova: ' + e);
    if (err && typeof err === 'function') {
      err(e);
    }
  };
  
  exec(successCallback, errorCallback, "MiViewTVPlugin", "getProgramGuide", [day]);
};

module.exports = MiViewTV;
