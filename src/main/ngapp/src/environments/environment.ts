// The file contents for the current environment will overwrite these during build.
// The build system defaults to the dev environment which uses `environment.ts`, but if you do
// `ng build --env=prod` then `environment.prod.ts` will be used instead.
// The list of which env maps to which file can be found in `.angular-cli.json`.

export const environment = {
  production: false,
  appEnvVersion: 'ng Dev v1.6',
  
  hiveAllScheduled : 'http://192.168.1.103:8080/api/instrschedule/public/list.all',
  hiveRemoveScheduled : 'http://192.168.1.103:8080/api/instrschedule/secure/remove',

  mclientGetInfo : 'http://192.168.1.103:8080/api/hivecentral/public/get.info',
  mclientSaveSettings : 'http://192.168.1.103:8080/api/hivecentral/secure/save.func.',
  mclientChartBaseURL : 'http://192.168.1.103:8080/api/sensorchart/public/temp_humidity',

  mclientApiLoginEndpoint : 'http://192.168.1.103:8080/app/security/login',
  mclientApiLogoutEndpoint : 'http://192.168.1.103:8080/app/security/logout',
  mclientApiCheckSecureAccess : 'http://192.168.1.103:8080/api/session/secure/check.access',

  mclimateBotId : 'OOMM.HIVE MICLIM.02',
  mclimateBotAccessKey : 'b293c9a090'
};
