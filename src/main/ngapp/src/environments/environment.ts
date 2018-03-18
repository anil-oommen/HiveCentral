// The file contents for the current environment will overwrite these during build.
// The build system defaults to the dev environment which uses `environment.ts`, but if you do
// `ng build --env=prod` then `environment.prod.ts` will be used instead.
// The list of which env maps to which file can be found in `.angular-cli.json`.

export const environment = {
  production: false,
  appEnvVersion: 'ng Dev v0.5',
  hiveAllScheduled : 'http://192.168.1.103:8080/hivecentral/all.scheduled',
  hiveRemoveScheduled : 'http://192.168.1.103:8080/hivecentral/remove.scheduled',
  mclientBaseURL : 'http://192.168.1.103:8080/hivecentral/iot.bot/xchange/',
  mclientChartBaseURL : 'http://192.168.1.103:8080/sensorchart/',
  mclimateBotId : 'OOMM.HIVE MICLIM.02',
  mclimateBotAccessKey : 'b293c9a090'
};
