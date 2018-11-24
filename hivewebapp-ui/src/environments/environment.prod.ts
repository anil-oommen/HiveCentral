

export const environment = {
  production: true,

  /* Same Value, as envirionment.ts , TODO check how base config can be created */
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
