// it.todo('write a test');

jest.mock('react-native-device-info', () => {});
jest.mock('react-native-geolocation-service', () => {});
jest.mock('react-native-bluetooth-status', () => {});
jest.mock('@react-native-async-storage/async-storage', () => {});

import { validateUserId } from '../../src/util/Validator';
import { emit, init, Region } from '../index';

// for thie init method test please comment the setItem calls from the original method
test('Call for init method for correct value', () => {
  return init('123', Region.US1).then((data) => {
    expect(data).toBe(undefined);
  });
});

test('Call for init method for incorrect api key', () => {
  return init('', Region.ASIA2).catch((data) => {
    expect(data).toBe('Please pass the valid APIkey');
  });
});

test('Call for init method for incorrect region', () => {
  return init('123', null).catch((data) => {
    expect(data).toBe('Please pass the valid Region');
  });
});

test('Call for emit method for valid userId', () => {
  expect(validateUserId('1234')).toBeTruthy();
});

test('Call for emit method for null userId', () => {
  expect(validateUserId(null)).toBeFalsy();
});

test('Call for emit method for empty userId', () => {
  expect(validateUserId('')).toBeFalsy();
});

test('Call for emit method for empty with spaces user Id', () => {
  expect(validateUserId('   ')).toBeFalsy();
});
