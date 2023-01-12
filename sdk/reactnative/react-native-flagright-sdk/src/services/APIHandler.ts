import type { RequestType } from 'src/types/RequestType';

const BASE_URL =
  'https://stoplight.io/mocks/flagright-device-api/flagright-device-data-api/122980601/';
const API_NAME = 'device/metric';

export function sendData(
  apiKey: string,
  requestParams: RequestType
): Promise<any> {
  return new Promise((resolve, reject) => {
    var myHeaders = new Headers();
    myHeaders.append('x-api-key', apiKey);
    myHeaders.append('Content-Type', 'application/json');

    var raw = JSON.stringify(requestParams);

    var requestOptions = {
      method: 'POST',
      headers: myHeaders,
      body: raw,
      redirect: 'follow',
    };

    fetch(BASE_URL.concat(API_NAME), requestOptions)
      // .then((response) => response.text())
      // .then((result) => console.log(result))
      .then((response) => {
        if (response.status === 200) {
          resolve('success');
        }
      })
      .catch((error) => reject(error));
  });
}
