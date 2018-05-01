

export const environment = {
  production: true,
  appEnvVersion: 'Production v1.6',

  hiveAllScheduled : '/api/instrschedule/public/list.all',
  hiveRemoveScheduled : '/api/instrschedule/secure/remove',
  


  mclientGetInfo : '/api/hivecentral/public/get.info',
  mclientSaveSettings : '/api/hivecentral/secure/save.func.',
  mclientChartBaseURL : '/api/sensorchart/public/temp_humidity',

  mclientApiLoginEndpoint : '/app/security/login',
  mclientApiLogoutEndpoint : '/app/security/logout',
  mclientApiCheckSecureAccess : '/api/session/secure/check.access',


  mclimateBotId : 'OOMM.HIVE MICLIM.02',
  mclimateBotAccessKey : 'b293c9a090'
};
