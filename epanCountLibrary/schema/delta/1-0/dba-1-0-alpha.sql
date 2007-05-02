ask DBA to do the following
---------------------------
(note: might as well do alpha and beta at the same time)




create users ALPHA_AE, and BETA_AE (set up similar to actual AE users)

create role SSI_AE
assign role SSI_AE to ALPHA_AE, and BETA_AE (as a default role)



assign role SSI_EMAIL_COUNT to SSI_AE (not as a default role)
