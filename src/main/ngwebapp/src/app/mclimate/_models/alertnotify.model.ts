export class Alert {
    type: AlertType;
    message: string;
}

export class LoadingState{
    stateOn:boolean=false;
}


export enum AlertType {
    Success,
    Error,
    Info,
    Warning
}

export interface LoaderStateRemoved {
    show: boolean;
}