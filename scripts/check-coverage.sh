#!/usr/bin/env bash
# Combines backend JaCoCo line coverage with frontend Vitest line coverage
# and fails if the blended total is below the project's 80% gate (CLAUDE.md rule 6).
set -euo pipefail

THRESHOLD=80
BACKEND_MODULES="domain application adapter-persistence adapter-web"

be_covered=0
be_total=0
for m in $BACKEND_MODULES; do
  csv="backend/$m/target/site/jacoco/jacoco.csv"
  if [ -f "$csv" ]; then
    read -r lm lc < <(awk -F, 'NR>1 {lm+=$8; lc+=$9} END {print lm, lc}' "$csv")
    be_total=$((be_total + lm + lc))
    be_covered=$((be_covered + lc))
  fi
done

fe_summary="frontend/coverage/frontend/coverage-summary.json"
if [ -f "$fe_summary" ]; then
  fe_covered=$(node -e "console.log(require('./$fe_summary').total.lines.covered)")
  fe_total=$(node -e "console.log(require('./$fe_summary').total.lines.total)")
else
  echo "WARNING: $fe_summary not found, frontend coverage excluded from combined total"
  fe_covered=0
  fe_total=0
fi

combined_covered=$((be_covered + fe_covered))
combined_total=$((be_total + fe_total))

if [ "$combined_total" -eq 0 ]; then
  echo "ERROR: no coverage data found (backend and frontend both empty)"
  exit 1
fi

pct=$(awk -v c="$combined_covered" -v t="$combined_total" 'BEGIN{printf "%.1f", 100*c/t}')

echo "Backend line coverage:  $be_covered/$be_total"
echo "Frontend line coverage: $fe_covered/$fe_total"
echo "Combined line coverage: $combined_covered/$combined_total ($pct%)"

if awk -v p="$pct" -v th="$THRESHOLD" 'BEGIN{exit !(p+0 < th)}'; then
  echo "FAIL: combined coverage $pct% is below the $THRESHOLD% gate"
  exit 1
fi

echo "PASS: combined coverage $pct% meets the $THRESHOLD% gate"
