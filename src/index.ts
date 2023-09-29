import { registerPlugin } from '@capacitor/core';

import type { UsbpermissionPlugin } from './definitions';

const Usbpermission = registerPlugin<UsbpermissionPlugin>('Usbpermission', {
  web: () => import('./web').then(m => new m.UsbpermissionWeb()),
});

export * from './definitions';
export { Usbpermission };
