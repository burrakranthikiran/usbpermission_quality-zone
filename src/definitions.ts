export interface UsbpermissionPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
