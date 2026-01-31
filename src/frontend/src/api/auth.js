export async function loginOrRegister(username, password) {
  const res = await fetch("https://huzhang-production.up.railway.app/auth/login-or-register", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username, password }),
  });

  // 401: 密码错误
  if (res.status === 401) {
    const text = await res.text().catch(() => "");
    throw new Error("INVALID_PASSWORD" + (text ? (": " + text) : ""));
  }

  if (!res.ok) {
    const text = await res.text().catch(() => "");
    throw new Error("REQUEST_FAILED" + (text ? (": " + text) : ""));
  }

  return await res.json(); // { id, username, mode }
}
