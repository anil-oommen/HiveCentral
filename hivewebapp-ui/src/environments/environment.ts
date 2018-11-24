// This file can be replaced during build by using the `fileReplacements` array.
// `ng build ---prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

// The file contents for the current environment will overwrite these during build.
// The build system defaults to the dev environment which uses `environment.ts`, but if you do
// `ng build --env=prod` then `environment.prod.ts` will be used instead.
// The list of which env maps to which file can be found in `.angular-cli.json`.

export const environment = {
  production: false,
  appEnvVersion: 'v2.2.0',

  hiveAllScheduled : '/api/instrschedule/public/list.all',
  hiveRemoveScheduled : '/api/instrschedule/secure/remove',

  mclientGetInfo : '/api/hivecentral/public/get.info',
  mclientSaveSettings : '/api/hivecentral/secure/save.func.',
  mclientChartBaseURL : '/api/sensorchart/public/temp_humidity',

  mclientApiLoginEndpoint : '/app/security/login',
  mclientApiLogoutEndpoint : '/app/security/logout',
  mclientApiCheckSecureAccess : '/api/session/secure/check.access',

  mclimateBotId : 'HIVEBOT_MICLIM.03',
  mclimateBotAccessKey : '1b4b882772c'
};


/*
 * In development mode, to ignore zone related error stack frames such as
 * `zone.run`, `zoneDelegate.invokeTask` for easier debugging, you can
 * import the following file, but please comment it out in production mode
 * because it will have performance impact when throw error
 */
// import 'zone.js/dist/zone-error';  // Included with Angular CLI.
