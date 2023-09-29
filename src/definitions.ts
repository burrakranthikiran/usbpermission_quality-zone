export interface UsbpermissionPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
  getUsbpermission(filter: string): Promise<{results: any[]}>;
}
