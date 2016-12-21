/*global cordova, module*/
/*
var utils = require('cordova/utils'),
  exec = require('cordova/exec'),
  cordova = require('cordova');

function MiViewTV () {
  this.channels = null;
  this.programGuide = null;
}

MiViewTV.prototype.getChannels = function (success, err) {
  var successCallback, errorCallback, self;
  
  self = this;
  
  successCallback = function (result) {
    self.channels = result;
    if (success && typeof success === 'function') {
      success(result);
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

MiViewTV.prototype.getProgramGuide = function (day, success, err) {
  var successCallback, errorCallback, self;
  
  self = this;
  
  successCallback = function (result) {
    self.programGuide = result;
    if (success && typeof success === 'function') {
      success(result);
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
*/

module.exports = {
    getChannels: function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "MiViewTVPlugin", "getChannels", []);
    },

    getProgramGuide: function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "MiViewTVPlugin", "getProgramGuide", []);
    }
};
