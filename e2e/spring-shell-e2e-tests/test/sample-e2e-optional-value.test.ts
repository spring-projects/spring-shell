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

// e2e optional value commands
describe('e2e commands optional-value', () => {
  let cli: Cli;
  let command: string;
  let options: string[] = [];

  /**
   * testOptionalValue
   */
  const annoOptionalWithoutArgReturnsNullDesc = 'optional without arg returns null (anno)';
  const annoOptionalWithoutArgCommand = ['e2e', 'anno', 'optional-value'];
  const annoOptionalWithoutArgReturnsNull = async (cli: Cli) => {
    cli.run();
    await waitForExpect(async () => {
      const screen = cli.screen();
      expect(screen).toEqual(expect.arrayContaining([expect.stringContaining('Hello null')]));
    });
    await expect(cli.exitCode()).resolves.toBe(0);
  };

  /**
   * testOptionalValueRegistration
   */
  const regOptionalWithoutArgReturnsNullDesc = 'optional without arg returns null (reg)';
  const regOptionalWithoutArgCommand = ['e2e', 'reg', 'optional-value'];
  const regOptionalWithoutArgReturnsNull = async (cli: Cli) => {
    cli.run();
    await waitForExpect(async () => {
      const screen = cli.screen();
      expect(screen).toEqual(expect.arrayContaining([expect.stringContaining('Hello null')]));
    });
    await expect(cli.exitCode()).resolves.toBe(0);
  };

  /**
   * testOptionalValue
   */
  const annoOptionalWithArgReturnsNullDesc = 'optional with arg returns hi (anno)';
  const annoOptionalWithArgCommand = ['e2e', 'anno', 'optional-value', '--arg1', 'hi'];
  const annoOptionalWithArgReturnsNull = async (cli: Cli) => {
    cli.run();
    await waitForExpect(async () => {
      const screen = cli.screen();
      expect(screen).toEqual(expect.arrayContaining([expect.stringContaining('Hello hi')]));
    });
    await expect(cli.exitCode()).resolves.toBe(0);
  };

  /**
   * testOptionalValueRegistration
   */
  const regOptionalWithArgReturnsHiDesc = 'optional with arg returns hi (reg)';
  const regOptionalWithArgCommand = ['e2e', 'reg', 'optional-value', '--arg1', 'hi'];
  const regOptionalWithArgReturnsHi = async (cli: Cli) => {
    cli.run();
    await waitForExpect(async () => {
      const screen = cli.screen();
      expect(screen).toEqual(expect.arrayContaining([expect.stringContaining('Hello hi')]));
    });
    await expect(cli.exitCode()).resolves.toBe(0);
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
      annoOptionalWithoutArgReturnsNullDesc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...annoOptionalWithoutArgCommand]
        });
        await annoOptionalWithoutArgReturnsNull(cli);
      },
      testTimeout
    );

    it(
      regOptionalWithoutArgReturnsNullDesc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...regOptionalWithoutArgCommand]
        });
        await regOptionalWithoutArgReturnsNull(cli);
      },
      testTimeout
    );

    it(
      annoOptionalWithArgReturnsNullDesc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...annoOptionalWithArgCommand]
        });
        await annoOptionalWithArgReturnsNull(cli);
      },
      testTimeout
    );

    it(
      regOptionalWithArgReturnsHiDesc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...regOptionalWithArgCommand]
        });
        await regOptionalWithArgReturnsHi(cli);
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
      annoOptionalWithoutArgReturnsNullDesc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...annoOptionalWithoutArgCommand]
        });
        await annoOptionalWithoutArgReturnsNull(cli);
      },
      testTimeout
    );

    it(
      regOptionalWithoutArgReturnsNullDesc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...regOptionalWithoutArgCommand]
        });
        await regOptionalWithoutArgReturnsNull(cli);
      },
      testTimeout
    );

    it(
      annoOptionalWithArgReturnsNullDesc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...annoOptionalWithArgCommand]
        });
        await annoOptionalWithArgReturnsNull(cli);
      },
      testTimeout
    );

    it(
      regOptionalWithArgReturnsHiDesc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...regOptionalWithArgCommand]
        });
        await regOptionalWithArgReturnsHi(cli);
      },
      testTimeout
    );
  });
});
