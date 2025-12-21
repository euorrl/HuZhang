<template>
  <div>
    <h1>Best Bike Paths (BBP)</h1>
    <p class="sub">
      An information-support system for cyclists: explore routes, consult community reports, and record personal trips.
    </p>

    <div class="grid">
      <div class="card">
        <h3>Quick actions</h3>
        <div class="actions">
          <RouterLink class="btn" to="/explore">Explore routes</RouterLink>
          <RouterLink class="btn" to="/community">Consult community info</RouterLink>
        </div>
      </div>

      <div class="card">
        <h3>Guest vs Registered</h3>
        <ul>
          <li><b>Guest:</b> explore routes, read <i>PUBLIC</i> reports</li>
          <li><b>Registered:</b> record trips, create path reports, choose PRIVATE/PUBLIC</li>
        </ul>
      </div>

      <div class="card">
        <h3>Privacy / Publish</h3>
        <p>
          Reports can be <b>PRIVATE</b> (only you) or <b>PUBLIC</b> (shared with the community).
        </p>
      </div>
    </div>

    <div class="card">
      <h3>Map placeholder</h3>
      <div class="mapbox">
        <div class="hint">Map will be integrated here (later: Leaflet / Mapbox).</div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.sub { color: #555; margin-top: 8px; }
.grid { display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 14px; margin-top: 14px; }
.card { border: 1px solid #eee; border-radius: 12px; padding: 14px; }
.actions { display:flex; gap: 10px; margin-top: 10px; flex-wrap: wrap; }
.btn {
  display:inline-block; padding: 10px 12px; border-radius: 10px;
  text-decoration: none; background: #f2f2f2; color:#111;
}
.mapbox { height: 360px; border: 1px dashed #bbb; border-radius: 12px; display:flex; align-items:center; justify-content:center; margin-top: 10px; }
.hint { color: #666; }
@media (max-width: 1000px) { .grid { grid-template-columns: 1fr; } }
</style>
