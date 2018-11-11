// This file can be replaced during build by using the `fileReplacements` array.
// `ng build ---prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

// The file contents for the current environment will overwrite these during build.
// The build system defaults to the dev environment which uses `environment.ts`, but if you do
// `ng build --env=prod` then `environment.prod.ts` will be used instead.
// The list of which env maps to which file can be found in `.angular-cli.json`.

export const environment = {
  production: false,
  appEnvVersion: 'ng Dev v2.1',

  hiveAllScheduled : 'http://192.168.1.103:8080/api/instrschedule/public/list.all',
  hiveRemoveScheduled : 'http://192.168.1.103:8080/api/instrschedule/secure/remove',

  mclientGetInfo : 'http://192.168.1.103:8080/api/hivecentral/public/get.info',
  mclientSaveSettings : 'http://192.168.1.103:8080/api/hivecentral/secure/save.func.',
  mclientChartBaseURL : 'http://192.168.1.103:8080/api/sensorchart/public/temp_humidity',

  mclientApiLoginEndpoint : 'http://192.168.1.103:8080/app/security/login',
  mclientApiLogoutEndpoint : 'http://192.168.1.103:8080/app/security/logout',
  mclientApiCheckSecureAccess : 'http://192.168.1.103:8080/api/session/secure/check.access',

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
