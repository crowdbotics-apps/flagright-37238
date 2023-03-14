import type { RequestType } from 'src/types/RequestType';

const BASE_URL =
  'https://stoplight.io/mocks/flagright-device-api/flagright-device-data-api/122980601/';
const API_NAME = 'device/metric';
const RESPONSE_SUCCESS = 200;

export function sendData(
  apiKey: string,
  requestParams: RequestType
): Promise<any> {
  return new Promise((resolve, reject) => {
    const myHeaders = new Headers();
    myHeaders.append('x-api-key', apiKey);
    myHeaders.append('Content-Type', 'application/json');

    const raw = JSON.stringify(requestParams);

    const requestOptions = {
      method: 'POST',
      headers: myHeaders,
      body: raw,
      redirect: 'follow',
    };

    fetch(BASE_URL.concat(API_NAME), requestOptions)
      // .then((response) => response.text())
      // .then((result) => console.log(result))
      .then((response) => {
        if (response.status === RESPONSE_SUCCESS) {
          resolve('success');
        }
      })
      .catch((error) => reject(error));
  });
}
