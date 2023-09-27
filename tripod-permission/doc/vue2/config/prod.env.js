'use strict'

const scriptName = process.env.npm_lifecycle_event;
var envv = 'prd';
if (scriptName.indexOf("build:") == 0) {
  envv = scriptName.substring("build:".length);
}

module.exports = {
  NODE_ENV: '"production"',
  ENV: '"' + envv + '"'
}
