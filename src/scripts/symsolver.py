import sys, json
from sympy import And, Or, Not, simplify_logic, sympify

raw = sys.stdin.read()
throw_condition_paths = json.loads(raw)
# throw condition paths are paths that have lists of conditions
# throw_condition_paths = [
#     [ {"truthValue": True,  "conditionStmt": "p < 0"} ],
#     [ {"truthValue": False, "conditionStmt": "p < 0"},
#       {"truthValue": False, "conditionStmt": "p <= 1"} ]
# ]

dnf = Or(*[
    And(*[
        sympify(condition["conditionStmt"]) if condition["truthValue"] else Not(sympify(condition["conditionStmt"]))
        for condition in condition_path
    ])
    for condition_path in throw_condition_paths
])

simplified = simplify_logic(dnf, form='dnf')

# Outputs to stdout. Java code expects a single line atm.
print(simplified)