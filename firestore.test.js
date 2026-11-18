const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";
const ADMIN_UID = "admin_999";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
    await testEnv.withSecurityRulesDisabled(async (ctx) => {
      await ctx.firestore().collection("admins").doc(ADMIN_UID).set({ active: true });
    });
  }
});

test("Unauthenticated users cannot read or write any collection", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(unauthDb.collection("leaderboards").get());
  await assertFails(unauthDb.collection("announcements").get());
  await assertFails(unauthDb.collection("questions").get());
});

test("User profile: owner can create player profile with zero authoritative stats and update nickname only", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const now = new Date(Date.now() - 1000);

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      nickname: "استاد حسابگر",
      selectedCharacterId: "hesabgar",
      departmentType: "شعبه نمونه",
      role: "player",
      authoritativeXp: 0,
      authoritativeCoins: 0,
      authoritativeMedals: 0,
      authoritativeScore: 0,
      createdAt: now,
      updatedAt: now,
    })
  );

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).update({
      nickname: "قهرمان تأمین",
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Reject self-promotion to admin or tampering with authoritativeScore
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      role: "admin",
      updatedAt: new Date(Date.now() - 500),
    })
  );
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      authoritativeScore: 99999,
      updatedAt: new Date(Date.now() - 500),
    })
  );
  await assertFails(
    aliceDb.collection("users").doc(BOB_UID).set({
      userId: BOB_UID,
      nickname: "جعل",
      selectedCharacterId: "hesabgar",
      role: "player",
      authoritativeXp: 0,
      authoritativeCoins: 0,
      authoritativeMedals: 0,
      authoritativeScore: 0,
      createdAt: now,
      updatedAt: now,
    })
  );
});

test("GameSessions: player can submit PENDING session, cannot self-verify or read another player's sessions", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const adminDb = testEnv.authenticatedContext(ADMIN_UID).firestore();
  const now = new Date(Date.now() - 1000);

  await assertSucceeds(
    aliceDb.collection("gameSessions").doc("op_12345678").set({
      userId: ALICE_UID,
      operationId: "op_12345678",
      stageId: 1,
      miniGameType: "VAULT_PUZZLE",
      rawScore: 150,
      xpClaimed: 100,
      coinsClaimed: 25,
      rating: "GOLD",
      durationSeconds: 24,
      correctCount: 3,
      totalCount: 3,
      verificationStatus: "PENDING",
      createdAt: now,
      updatedAt: now,
    })
  );

  // Reject pre-verified spoofed session from player
  await assertFails(
    aliceDb.collection("gameSessions").doc("op_spoof1234").set({
      userId: ALICE_UID,
      operationId: "op_spoof1234",
      stageId: 1,
      miniGameType: "VAULT_PUZZLE",
      rawScore: 150,
      xpClaimed: 100,
      coinsClaimed: 25,
      rating: "GOLD",
      verificationStatus: "VERIFIED",
      createdAt: now,
      updatedAt: now,
    })
  );

  // Filtered query succeeds, unfiltered query fails, cross-user read fails
  await assertSucceeds(aliceDb.collection("gameSessions").where("userId", "==", ALICE_UID).get());
  await assertFails(aliceDb.collection("gameSessions").get());
  await assertFails(bobDb.collection("gameSessions").doc("op_12345678").get());

  // Player cannot self-verify, admin can verify
  await assertFails(
    aliceDb.collection("gameSessions").doc("op_12345678").update({
      verificationStatus: "VERIFIED",
      updatedAt: new Date(Date.now() - 200),
    })
  );
  await assertSucceeds(
    adminDb.collection("gameSessions").doc("op_12345678").update({
      verificationStatus: "VERIFIED",
      updatedAt: new Date(Date.now() - 200),
    })
  );
});

test("Questions & Leaderboards: public clients cannot read questions answers or forge leaderboard", async () => {
  await testEnv.withSecurityRulesDisabled(async (ctx) => {
    await ctx.firestore().collection("questions").doc("q_1").set({
      questionText: "سوال محرمانه",
      correctOptionIndex: 2,
      category: "SPECIALIZED",
      publicationStatus: "PUBLISHED",
    });
    await ctx.firestore().collection("leaderboards").doc("entry_1").set({
      userId: BOB_UID,
      nickname: "مهندس پویا",
      selectedCharacterId: "hesabgar",
      level: 5,
      score: 12500,
      updatedAt: new Date(Date.now() - 1000),
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const adminDb = testEnv.authenticatedContext(ADMIN_UID).firestore();

  await assertFails(aliceDb.collection("questions").doc("q_1").get());
  await assertSucceeds(adminDb.collection("questions").doc("q_1").get());

  await assertSucceeds(aliceDb.collection("leaderboards").get());
  await assertFails(
    aliceDb.collection("leaderboards").doc("entry_alice").set({
      userId: ALICE_UID,
      nickname: "هکر",
      selectedCharacterId: "hesabgar",
      level: 8,
      score: 999999,
      updatedAt: new Date(Date.now() - 1000),
    })
  );
});
