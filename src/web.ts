import { WebPlugin } from '@capacitor/core';

import type { UsbpermissionPlugin } from './definitions';

export class UsbpermissionWeb extends WebPlugin implements UsbpermissionPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
