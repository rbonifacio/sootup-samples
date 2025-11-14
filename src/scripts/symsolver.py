import sys, json
from sympy import S, And, Or, Not, simplify_logic, sympify
import z3
from z3 import Solver, sat, unsat
import re

# on the shoulders of giants
# https://github.com/mmaaz-git/sym2z/blob/main/sym2z.py
def _sympy_to_z3(expr):
    variables = set(expr.free_symbols)

    expr_str = str(expr)
    for var in variables:
        # Add word boundaries to only replace whole variable names
        var_str = r'\b' + str(var) + r'\b'
        if var.is_integer:
            expr_str = re.sub(var_str, f"z3.Int('{var}')", expr_str)
        else:
            expr_str = re.sub(var_str, f"z3.Real('{var}')", expr_str)

    # Boolean operators
    expr_str = expr_str.replace("And", "z3.And")
    expr_str = expr_str.replace("Or", "z3.Or")
    expr_str = expr_str.replace("Not", "z3.Not")

    return eval(expr_str)

def check_feasibility(system):
    if not isinstance(system, list):
        system = [system]

    variables = set()
    for expr in system:
        variables.update(expr.free_symbols)

    s = Solver()
    for expr in system:
        s.add(_sympy_to_z3(expr))

    if s.check() == sat:
        model = s.model()
        result = {}
        solutions = []
        for var in variables:
            z3_var = z3.Int(str(var)) if var.is_integer else z3.Real(str(var))
            value = model.get_interp(z3_var)
            result[var] = S(str(value))
            solution = {
                "variable": str(var),
                "value": str(value)
            }
            solutions.append(solution)
        return {
            "is_sat": True,
            "solutions": solutions,
        }
    elif s.check() == unsat:
        return "unsat", {}
    else:
        return "unknown", {}

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
print(json.dumps(check_feasibility(simplified)))