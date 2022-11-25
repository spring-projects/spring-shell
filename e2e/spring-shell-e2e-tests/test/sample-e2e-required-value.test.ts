import 'jest-extended';
import waitForExpect from 'wait-for-expect';
import { Cli } from 'spring-shell-e2e';
import {
  nativeDesc,
  jarDesc,
  jarCommand,
  nativeCommand,
  jarOptions,
  waitForExpectDefaultTimeout,
  waitForExpectDefaultInterval,
  testTimeout
} from '../src/utils';

describe('e2e commands required-value', () => {
  let cli: Cli;
  let command: string;
  let options: string[] = [];

  const annoRequiredWithoutArgReturnsErrorDesc = 'required without arg returns error (anno)';
  const annoRequiredWithoutArgCommand = ['e2e', 'anno', 'required-value'];
  const annoRequiredWithoutArgReturnsError = async (cli: Cli) => {
    cli.run();
    await waitForExpect(async () => {
      const screen = cli.screen();
      expect(screen).toEqual(expect.arrayContaining([expect.stringContaining('Missing mandatory option')]));
    });
    await expect(cli.exitCode()).resolves.toBe(2);
  };

  const regRequiredWithoutArgReturnsErrorDesc = 'required without arg returns error (reg)';
  const regRequiredWithoutArgCommand = ['e2e', 'reg', 'required-value'];
  const regRequiredWithoutArgReturnsError = async (cli: Cli) => {
    cli.run();
    await waitForExpect(async () => {
      const screen = cli.screen();
      expect(screen).toEqual(expect.arrayContaining([expect.stringContaining('Missing mandatory option')]));
    });
    await expect(cli.exitCode()).resolves.toBe(2);
  };

  beforeEach(async () => {
    waitForExpect.defaults.timeout = waitForExpectDefaultTimeout;
    waitForExpect.defaults.interval = waitForExpectDefaultInterval;
  }, testTimeout);

  afterEach(async () => {
    cli?.dispose();
  }, testTimeout);

  /**
   * fatjar commands
   */
  describe(jarDesc, () => {
    beforeAll(() => {
      command = jarCommand;
      options = jarOptions;
    });

    it(
      annoRequiredWithoutArgReturnsErrorDesc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...annoRequiredWithoutArgCommand]
        });
        await annoRequiredWithoutArgReturnsError(cli);
      },
      testTimeout
    );

    it(
      regRequiredWithoutArgReturnsErrorDesc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...regRequiredWithoutArgCommand]
        });
        await regRequiredWithoutArgReturnsError(cli);
      },
      testTimeout
    );
  });

  /**
   * native commands
   */
  describe(nativeDesc, () => {
    beforeAll(() => {
      command = nativeCommand;
      options = [];
    });

    it(
      annoRequiredWithoutArgReturnsErrorDesc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...annoRequiredWithoutArgCommand]
        });
        await annoRequiredWithoutArgReturnsError(cli);
      },
      testTimeout
    );

    it(
      regRequiredWithoutArgReturnsErrorDesc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...regRequiredWithoutArgCommand]
        });
        await regRequiredWithoutArgReturnsError(cli);
      },
      testTimeout
    );
  });
});
