# react-native-flagright-sdk

Flagright SDK

## Installation

```sh
npm install react-native-flagright-sdk
```

or

```sh
yarn add react-native-flagright-sdk
```

## Usage

```js
import { init } from 'react-native-flagright-sdk';

// ...

const result = await init('123', '1234');
```

With transaction id

```js
const result = await init("123", "1234", "12345678);
```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
