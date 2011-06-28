ask DBA to do the following:
----------------------------
for SPOT: DA_Prod.SSIEmployee.userName column,
make sure it is set correctly for *all* AE users
(note: this should be done for all users, if desired)


create role SSI_AE
assign role SSI_AE to *all* AE users (as a default role)


assign role SSI_EMAIL_COUNT to SSI_AE (not as a default role)
