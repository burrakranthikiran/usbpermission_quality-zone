import { WebPlugin } from '@capacitor/core';

import type { UsbpermissionPlugin } from './definitions';

export class UsbpermissionWeb extends WebPlugin implements UsbpermissionPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
  async getUsbpermission(filter: string): Promise<{ results: any[] }> {
    console.log('filter: ', filter);
    return {
      results: [{
        firstName: 'Dummy',
        lastName: 'Entry',
        telephone: '123456'
      }]
    };
  }
}
