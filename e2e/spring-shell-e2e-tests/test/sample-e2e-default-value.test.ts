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

// e2e default value commands
describe('e2e commands default-value', () => {
  let cli: Cli;
  let command: string;
  let options: string[] = [];

  /**
   * testDefaultValue
   */
  const annoDefaultWithoutArgReturnsHiDesc = 'default without arg returns hi (anno)';
  const annoDefaultWithoutArgCommand = ['e2e', 'anno', 'default-value'];
  const annoDefaultWithoutArgReturnsHi = async (cli: Cli) => {
    cli.run();
    await waitForExpect(async () => {
      const screen = cli.screen();
      expect(screen).toEqual(expect.arrayContaining([expect.stringContaining('Hello hi')]));
    });
    await expect(cli.exitCode()).resolves.toBe(0);
  };

  /**
   * testDefaultValueRegistration
   */
  const regDefaultWithoutArgReturnsHiDesc = 'default without arg returns hi (reg)';
  const regDefaultWithoutArgCommand = ['e2e', 'reg', 'default-value'];
  const regOptionalWithoutArgReturnsHi = async (cli: Cli) => {
    cli.run();
    await waitForExpect(async () => {
      const screen = cli.screen();
      expect(screen).toEqual(expect.arrayContaining([expect.stringContaining('Hello hi')]));
    });
    await expect(cli.exitCode()).resolves.toBe(0);
  };

  /**
   * testDefaultValue
   */
  const annoDefaultWithArgReturnsFooDesc = 'default with arg returns foo (anno)';
  const annoDefaultWithArgCommand = ['e2e', 'anno', 'default-value', '--arg1', 'foo'];
  const annoDefaultWithArgReturnsFoo = async (cli: Cli) => {
    cli.run();
    await waitForExpect(async () => {
      const screen = cli.screen();
      expect(screen).toEqual(expect.arrayContaining([expect.stringContaining('Hello foo')]));
    });
    await expect(cli.exitCode()).resolves.toBe(0);
  };

  /**
   * testDefaultValueRegistration
   */
  const regDefaultWithArgReturnsFooDesc = 'default with arg returns foo (reg)';
  const regDefaultWithArgCommand = ['e2e', 'reg', 'optional-value', '--arg1', 'foo'];
  const regDefaultWithArgReturnsFoo = async (cli: Cli) => {
    cli.run();
    await waitForExpect(async () => {
      const screen = cli.screen();
      expect(screen).toEqual(expect.arrayContaining([expect.stringContaining('Hello foo')]));
    });
    await expect(cli.exitCode()).resolves.toBe(0);
  };

  /**
   * testDefaultValueBoolean1 - 1
   */
   const annoDefaultValueBoolean1WithoutArgReturnsFalseDesc = 'default boolean1 without arg returns false (anno)';
   const annoDefaultValueBoolean1WithoutArgCommand = ['e2e', 'anno', 'default-value-boolean1'];
   const annoDefaultValueBoolean1WithoutArgReturnsFalse = async (cli: Cli) => {
     cli.run();
     await waitForExpect(async () => {
       const screen = cli.screen();
       expect(screen).toEqual(expect.arrayContaining([expect.stringContaining('Hello false')]));
     });
     await expect(cli.exitCode()).resolves.toBe(0);
   };

  /**
   * testDefaultValueBoolean1Registration - 1
   */
   const regDefaultValueBoolean1WithoutArgReturnsFalseDesc = 'default boolean1 without arg returns false (reg)';
   const regDefaultValueBoolean1WithoutArgCommand = ['e2e', 'reg', 'default-value-boolean1'];
   const regDefaultValueBoolean1WithoutArgReturnsFalse = async (cli: Cli) => {
     cli.run();
     await waitForExpect(async () => {
       const screen = cli.screen();
       expect(screen).toEqual(expect.arrayContaining([expect.stringContaining('Hello false')]));
     });
     await expect(cli.exitCode()).resolves.toBe(0);
   };

  /**
   * testDefaultValueBoolean1 - 2
   */
   const annoDefaultValueBoolean2WithArgReturnsTrueDesc = 'default boolean1 with arg returns true (anno)';
   const annoDefaultValueBoolean2WithArgCommand = ['e2e', 'anno', 'default-value-boolean1', '--arg1'];
   const annoDefaultValueBoolean2WithArgReturnsTrue = async (cli: Cli) => {
     cli.run();
     await waitForExpect(async () => {
       const screen = cli.screen();
       expect(screen).toEqual(expect.arrayContaining([expect.stringContaining('Hello true')]));
     });
     await expect(cli.exitCode()).resolves.toBe(0);
   };

  /**
   * testDefaultValueBoolean1Registration - 2
   */
   const regDefaultValueBoolean2WithArgReturnsTrueDesc = 'default boolean1 with arg returns true (reg)';
   const regDefaultValueBoolean2WithArgCommand = ['e2e', 'reg', 'default-value-boolean1', '--arg1'];
   const regDefaultValueBoolean2WithArgReturnsTrue = async (cli: Cli) => {
     cli.run();
     await waitForExpect(async () => {
       const screen = cli.screen();
       expect(screen).toEqual(expect.arrayContaining([expect.stringContaining('Hello true')]));
     });
     await expect(cli.exitCode()).resolves.toBe(0);
   };

  /**
   * testDefaultValueBoolean1 - 3
   */
   const annoDefaultValueBoolean3WithArgReturnsFalseDesc = 'default boolean1 with arg returns false (anno)';
   const annoDefaultValueBoolean3WithArgCommand = ['e2e', 'anno', 'default-value-boolean1', '--arg1', 'false'];
   const annoDefaultValueBoolean3WithArgReturnsFalse = async (cli: Cli) => {
     cli.run();
     await waitForExpect(async () => {
       const screen = cli.screen();
       expect(screen).toEqual(expect.arrayContaining([expect.stringContaining('Hello false')]));
     });
     await expect(cli.exitCode()).resolves.toBe(0);
   };

  /**
   * testDefaultValueBoolean1Registration - 3
   */
   const regDefaultValueBoolean3WithArgReturnsFalseDesc = 'default boolean1 with arg returns false (reg)';
   const regDefaultValueBoolean3WithArgCommand = ['e2e', 'reg', 'default-value-boolean1', '--arg1', 'false'];
   const regDefaultValueBoolean3WithArgReturnsFalse = async (cli: Cli) => {
     cli.run();
     await waitForExpect(async () => {
       const screen = cli.screen();
       expect(screen).toEqual(expect.arrayContaining([expect.stringContaining('Hello false')]));
     });
     await expect(cli.exitCode()).resolves.toBe(0);
   };

  /**
   * testDefaultValueBoolean2 - 1
   */
   const annoDefaultValueBoolean2WithoutArgReturnsTrueDesc = 'default boolean2 without arg returns true (anno)';
   const annoDefaultValueBoolean2WithoutArgCommand = ['e2e', 'anno', 'default-value-boolean2'];
   const annoDefaultValueBoolean2WithoutArgReturnsTrue = async (cli: Cli) => {
     cli.run();
     await waitForExpect(async () => {
       const screen = cli.screen();
       expect(screen).toEqual(expect.arrayContaining([expect.stringContaining('Hello true')]));
     });
     await expect(cli.exitCode()).resolves.toBe(0);
   };

  /**
   * testDefaultValueBoolean2Registration - 1
   */
   const regDefaultValueBoolean2WithoutArgReturnsTrueDesc = 'default boolean2 without arg returns true (reg)';
   const regDefaultValueBoolean2WithoutArgCommand = ['e2e', 'reg', 'default-value-boolean2'];
   const regDefaultValueBoolean2WithoutArgReturnsTrue = async (cli: Cli) => {
     cli.run();
     await waitForExpect(async () => {
       const screen = cli.screen();
       expect(screen).toEqual(expect.arrayContaining([expect.stringContaining('Hello true')]));
     });
     await expect(cli.exitCode()).resolves.toBe(0);
   };

  /**
   * testDefaultValueBoolean3 - 1
   */
   const annoDefaultValueBoolean3WithoutArgReturnsFalseDesc = 'default boolean3 without arg returns false (anno)';
   const annoDefaultValueBoolean3WithoutArgCommand = ['e2e', 'anno', 'default-value-boolean3'];
   const annoDefaultValueBoolean3WithoutArgReturnsFalse = async (cli: Cli) => {
     cli.run();
     await waitForExpect(async () => {
       const screen = cli.screen();
       expect(screen).toEqual(expect.arrayContaining([expect.stringContaining('Hello false')]));
     });
     await expect(cli.exitCode()).resolves.toBe(0);
   };

  /**
   * testDefaultValueBoolean3Registration - 1
   */
   const regDefaultValueBoolean3WithoutArgReturnsFalseDesc = 'default boolean3 without arg returns false (reg)';
   const regDefaultValueBoolean3WithoutArgCommand = ['e2e', 'reg', 'default-value-boolean3'];
   const regDefaultValueBoolean3WithoutArgReturnsFalse = async (cli: Cli) => {
     cli.run();
     await waitForExpect(async () => {
       const screen = cli.screen();
       expect(screen).toEqual(expect.arrayContaining([expect.stringContaining('Hello false')]));
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
      annoDefaultWithoutArgReturnsHiDesc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...annoDefaultWithoutArgCommand]
        });
        await annoDefaultWithoutArgReturnsHi(cli);
      },
      testTimeout
    );

    it(
      regDefaultWithoutArgReturnsHiDesc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...regDefaultWithoutArgCommand]
        });
        await regOptionalWithoutArgReturnsHi(cli);
      },
      testTimeout
    );

    it(
      annoDefaultWithArgReturnsFooDesc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...annoDefaultWithArgCommand]
        });
        await annoDefaultWithArgReturnsFoo(cli);
      },
      testTimeout
    );

    it(
      regDefaultWithArgReturnsFooDesc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...regDefaultWithArgCommand]
        });
        await regDefaultWithArgReturnsFoo(cli);
      },
      testTimeout
    );

    it(
      annoDefaultValueBoolean1WithoutArgReturnsFalseDesc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...annoDefaultValueBoolean1WithoutArgCommand]
        });
        await annoDefaultValueBoolean1WithoutArgReturnsFalse(cli);
      },
      testTimeout
    );

    it(
      regDefaultValueBoolean1WithoutArgReturnsFalseDesc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...regDefaultValueBoolean1WithoutArgCommand]
        });
        await regDefaultValueBoolean1WithoutArgReturnsFalse(cli);
      },
      testTimeout
    );

    it(
      annoDefaultValueBoolean2WithArgReturnsTrueDesc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...annoDefaultValueBoolean2WithArgCommand]
        });
        await annoDefaultValueBoolean2WithArgReturnsTrue(cli);
      },
      testTimeout
    );

    it(
      regDefaultValueBoolean2WithArgReturnsTrueDesc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...regDefaultValueBoolean2WithArgCommand]
        });
        await regDefaultValueBoolean2WithArgReturnsTrue(cli);
      },
      testTimeout
    );

    it(
      annoDefaultValueBoolean3WithArgReturnsFalseDesc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...annoDefaultValueBoolean3WithArgCommand]
        });
        await annoDefaultValueBoolean3WithArgReturnsFalse(cli);
      },
      testTimeout
    );

    it(
      regDefaultValueBoolean3WithArgReturnsFalseDesc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...regDefaultValueBoolean3WithArgCommand]
        });
        await regDefaultValueBoolean3WithArgReturnsFalse(cli);
      },
      testTimeout
    );

    it(
      annoDefaultValueBoolean2WithoutArgReturnsTrueDesc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...annoDefaultValueBoolean2WithoutArgCommand]
        });
        await annoDefaultValueBoolean2WithoutArgReturnsTrue(cli);
      },
      testTimeout
    );

    it(
      regDefaultValueBoolean2WithoutArgReturnsTrueDesc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...regDefaultValueBoolean2WithoutArgCommand]
        });
        await regDefaultValueBoolean2WithoutArgReturnsTrue(cli);
      },
      testTimeout
    );

    it(
      annoDefaultValueBoolean3WithoutArgReturnsFalseDesc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...annoDefaultValueBoolean3WithoutArgCommand]
        });
        await annoDefaultValueBoolean3WithoutArgReturnsFalse(cli);
      },
      testTimeout
    );

    it(
      regDefaultValueBoolean3WithoutArgReturnsFalseDesc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...regDefaultValueBoolean3WithoutArgCommand]
        });
        await regDefaultValueBoolean3WithoutArgReturnsFalse(cli);
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
      annoDefaultWithoutArgReturnsHiDesc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...annoDefaultWithoutArgCommand]
        });
        await annoDefaultWithoutArgReturnsHi(cli);
      },
      testTimeout
    );

    it(
      regDefaultWithoutArgReturnsHiDesc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...regDefaultWithoutArgCommand]
        });
        await regOptionalWithoutArgReturnsHi(cli);
      },
      testTimeout
    );

    it(
      annoDefaultWithArgReturnsFooDesc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...annoDefaultWithArgCommand]
        });
        await annoDefaultWithArgReturnsFoo(cli);
      },
      testTimeout
    );

    it(
      regDefaultWithArgReturnsFooDesc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...regDefaultWithArgCommand]
        });
        await regDefaultWithArgReturnsFoo(cli);
      },
      testTimeout
    );

    it(
      annoDefaultValueBoolean1WithoutArgReturnsFalseDesc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...annoDefaultValueBoolean1WithoutArgCommand]
        });
        await annoDefaultValueBoolean1WithoutArgReturnsFalse(cli);
      },
      testTimeout
    );

    it(
      regDefaultValueBoolean1WithoutArgReturnsFalseDesc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...regDefaultValueBoolean1WithoutArgCommand]
        });
        await regDefaultValueBoolean1WithoutArgReturnsFalse(cli);
      },
      testTimeout
    );

    it(
      annoDefaultValueBoolean2WithArgReturnsTrueDesc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...annoDefaultValueBoolean2WithArgCommand]
        });
        await annoDefaultValueBoolean2WithArgReturnsTrue(cli);
      },
      testTimeout
    );

    it(
      regDefaultValueBoolean2WithArgReturnsTrueDesc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...regDefaultValueBoolean2WithArgCommand]
        });
        await regDefaultValueBoolean2WithArgReturnsTrue(cli);
      },
      testTimeout
    );

    it(
      annoDefaultValueBoolean3WithArgReturnsFalseDesc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...annoDefaultValueBoolean3WithArgCommand]
        });
        await annoDefaultValueBoolean3WithArgReturnsFalse(cli);
      },
      testTimeout
    );

    it(
      regDefaultValueBoolean3WithArgReturnsFalseDesc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...regDefaultValueBoolean3WithArgCommand]
        });
        await regDefaultValueBoolean3WithArgReturnsFalse(cli);
      },
      testTimeout
    );

    it(
      annoDefaultValueBoolean2WithoutArgReturnsTrueDesc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...annoDefaultValueBoolean2WithoutArgCommand]
        });
        await annoDefaultValueBoolean2WithoutArgReturnsTrue(cli);
      },
      testTimeout
    );

    it(
      regDefaultValueBoolean2WithoutArgReturnsTrueDesc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...regDefaultValueBoolean2WithoutArgCommand]
        });
        await regDefaultValueBoolean2WithoutArgReturnsTrue(cli);
      },
      testTimeout
    );

    it(
      annoDefaultValueBoolean3WithoutArgReturnsFalseDesc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...annoDefaultValueBoolean3WithoutArgCommand]
        });
        await annoDefaultValueBoolean3WithoutArgReturnsFalse(cli);
      },
      testTimeout
    );

    it(
      regDefaultValueBoolean3WithoutArgReturnsFalseDesc,
      async () => {
        cli = new Cli({
          command: command,
          options: [...options, ...regDefaultValueBoolean3WithoutArgCommand]
        });
        await regDefaultValueBoolean3WithoutArgReturnsFalse(cli);
      },
      testTimeout
    );
  });
});
