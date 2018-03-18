export class Alert {
    type: AlertType;
    message: string;
}

export enum AlertType {
    Success,
    Error,
    Info,
    Warning
}

export interface LoaderState {
    show: boolean;
}