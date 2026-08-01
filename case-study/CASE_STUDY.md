# Case Study Scenarios to discuss

## Scenario 1: Cost Allocation and Tracking
**Situation**: The company needs to track and allocate costs accurately across different Warehouses and Stores. The costs include labor, inventory, transportation, and overhead expenses.

**Task**: Discuss the challenges in accurately tracking and allocating costs in a fulfillment environment. Think about what are important considerations for this, what are previous experiences that you have you could related to this problem and elaborate some questions and considerations

**Questions you may have and considerations:**
> Shared costs are the real headache — one truck hitting three stores, one warehouse manager covering two sites. Need a fair split method and it has to stay consistent.
>
> Labor tracking is tough when people float between cost centers in one shift.
>
> Transportation allocation (by distance, weight, or stop count) can make a store look expensive or cheap depending on the method — that's a real risk.
>
> Overhead like rent and utilities usually gets split by square footage, it's not perfect but it's workable.
>
> Timing issues — invoices land late, so accruals matter, not just "when it hit the system."
>
> Questions: What's the current allocation method, if any? Who owns the rules, finance or ops? How granular do we need to go — per order, per store?

## Scenario 2: Cost Optimization Strategies
**Situation**: The company wants to identify and implement cost optimization strategies for its fulfillment operations. The goal is to reduce overall costs without compromising service quality.

**Task**: Discuss potential cost optimization strategies for fulfillment operations and expected outcomes from that. How would you identify, prioritize and implement these strategies?

**Questions you may have and considerations:**
> Labor and transport are usually 60-70% of the cost, so that's where to look first.
>
> Levers: better scheduling, route consolidation, smarter slotting, faster dock-to-stock.
>
> Prioritize by impact vs. effort — quick wins first, bigger structural changes later.
>
> Cost cuts can't come at the expense of service level — saving money by slowing delivery just moves the cost elsewhere.
>
> Need a clean baseline before touching anything — can't optimize what isn't measured.
>
> Pilot first, then scale. Too many programs get rolled out company-wide off a spreadsheet estimate and underdeliver.
>
> Questions: Is there an existing cost-per-order benchmark? What's the tolerance for service tradeoffs? Who signs off on changes that touch both ops and finance?

## Scenario 3: Integration with Financial Systems
**Situation**: The Cost Control Tool needs to integrate with existing financial systems to ensure accurate and timely cost data. The integration should support real-time data synchronization and reporting.

**Task**: Discuss the importance of integrating the Cost Control Tool with financial systems. What benefits the company would have from that and how would you ensure seamless integration and data synchronization?

**Questions you may have and considerations:**
> Trust and accuracy — if the numbers don't tie back to the official financial system, finance won't rely on the tool and will keep using their own numbers instead.
>
> Speed of decision-making — real-time or near-real-time data means problems like a warehouse going over budget get caught mid-month, not discovered weeks later at close.
>
> Eliminates duplicate work and errors — data flows automatically instead of being manually re-entered in two places, which cuts mistakes and saves time.
>
> Benefits: Accuracy — costs get pulled from source, not manually re-entered, so fewer errors
>
> Speed — real-time or near-real-time data means issues (like a warehouse going over budget) get caught mid-month instead of at month-end close
>
> Trust — one shared set of numbers means ops and finance stop arguing over whose report is correct
>
> Better reporting — leadership can see live cost performance by warehouse/store instead of waiting for a monthly close cycle
>
> Audit readiness — everything traces cleanly back to the GL, which matters for compliance and financial reviews
>
> Questions I'd ask: What ERP/financial system are we integrating with (SAP, Oracle, NetSuite, etc.)?
>
> Is there an existing API or middleware layer, or would this be custom-built?
>
> What's the tolerance for latency — is daily batch sync acceptable or does it need to be closer to real-time?
>
> Who owns data quality on each side when there's a mismatch?

## Scenario 4: Budgeting and Forecasting
**Situation**: The company needs to develop budgeting and forecasting capabilities for its fulfillment operations. The goal is to predict future costs and allocate resources effectively.

**Task**: Discuss the importance of budgeting and forecasting in fulfillment operations and what would you take into account designing a system to support accurate budgeting and forecasting?

**Questions you may have and considerations:**
> Forecast demand first — cost follows volume, so a bad volume forecast breaks everything downstream.
>
> Use a rolling forecast, not an annual budget — ops move too fast for a once-a-year number to stay accurate.
>
> Bake in seasonality — peak can hit 3-5x normal, so use historical patterns, not flat growth.
>
> Split fixed vs. variable costs — rent/base staffing are fixed, transport/overtime scale with volume. Blending them kills actionability.
>
> Prioritize variance analysis — show actual vs. budget by warehouse/store and why, not just red numbers.
>
> Questions:
>
> What horizon — monthly, quarterly, rolling 12?
>
> Any existing demand planning data to use?
>
> How do we model new/closed warehouses with no history?
>
> Who owns forecast accuracy — ops or finance?

## Scenario 5: Cost Control in Warehouse Replacement
**Situation**: The company is planning to replace an existing Warehouse with a new one. The new Warehouse will reuse the Business Unit Code of the old Warehouse. The old Warehouse will be archived, but its cost history must be preserved.

**Task**: Discuss the cost control aspects of replacing a Warehouse. Why is it important to preserve cost history and how this relates to keeping the new Warehouse operation within budget?

**Questions you may have and considerations:**
> Cost control:
>
> Reusing the Business Unit Code risks blending old and new warehouse costs — need a clear cutover date or sub-code to keep them separate.
> Transition costs (dual-running, moving inventory, training) should be tracked apart from steady-state costs, or they'll skew future budgets.
> New site needs its own cost baseline — different lease, staffing, equipment than the old one.
>
> Why preserve cost history:
>
> It's the baseline for forecasting and seasonal trends.
> Needed for audits/reporting even after archiving.
> Separates "old site wind-down cost" from "new site ramp-up cost" so each is judged fairly.
>
> Link to staying on budget:
>
> New warehouse should be budgeted from its actual go-live date, not inherit the old site's numbers.
> Startup inefficiency shouldn't be measured against mature-site targets.
> Clean separation is what makes actual-vs-budget variance analysis meaningful.
>
> Questions:
>
> What's the exact cutover date — sub-code needed under the shared Business Unit Code?
> Are transition costs one-time or operating?
> Does the new site get a fresh budget, or inherit the old one's remainder?
> Who owns reconciling costs — finance, ops, or IT?

## Instructions for Candidates
Before starting the case study, read the [BRIEFING.md](BRIEFING.md) to quickly understand the domain, entities, business rules, and other relevant details.

**Analyze the Scenarios**: Carefully analyze each scenario and consider the tasks provided. To make informed decisions about the project's scope and ensure valuable outcomes, what key information would you seek to gather before defining the boundaries of the work? Your goal is to bridge technical aspects with business value, bringing a high level discussion; no need to deep dive.
