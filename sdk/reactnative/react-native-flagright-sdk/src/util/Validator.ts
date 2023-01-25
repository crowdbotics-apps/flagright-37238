import { Region } from '../index';

function validateString(str: string) {
  if (str && str.trim().length > 0) {
    return true;
  }
  return false;
}

export function validateAPIKey(apiKey: string) {
  return validateString(apiKey);
}

export function validateUserId(userId: string) {
  return validateString(userId);
}

export function validateRegion(region: string) {
  if (region) {
    let validated = false;
    if (
      region === Region.ASIA1 ||
      region === Region.ASIA2 ||
      region === Region.EU1 ||
      region === Region.EU2 ||
      region === Region.US1
    )
      validated = true;
    return validated;
  }
  return false;
}
