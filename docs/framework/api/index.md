---
title: 准入条件
---

# 巨灵神规范

## 巨灵神准入条件

1. 两个及以上的服务或应用使用的接口
2. 非 MGR 网关使用的接口
3. 开放的 API 需要提供 javadoc 和版本追踪
4. 巨灵神的接口新增和变动将会有更严格的审核，提交时将必要信息填入 commit message 中。
   满足以下情况的，需要从现有巨灵神中移除
   1. 仅供一个应用或服务使用的接口，应将接口类信息直接维护在对应应用内。
   2. MGR 网关使用的接口，通过 KONG 网关配置直接映射使用。

## 接口归属详情

| 所属中心                        | api                                      |
| ------------------------------- | ---------------------------------------- |
| 会员中心 ciicgat-agg-member     | ciicgat-api-passport                     |
|                                 | ciicgat-api-userdoor                     |
|                                 | ciicgat-api-userdoorES                   |
|                                 | ciicgat-api-admin                        |
|                                 | ciicgat-api-log                          |
|                                 | ciicgat-api-riskrule                     |
|                                 | ciicgat-api-riskdata                     |
|                                 | ciicgat-api-risk-safety                  |
|                                 | ciicgat-api-device                       |
|                                 | ciicgat-api-wxwork                       |
|                                 | ciicgat-api-wechat                       |
|                                 | ciicgat-api-contract                     |
|                                 | ciicgat-api-contract-signing             |
| 资产中心 ciicgat-agg-asset      | ciicgat-api-assets                       |
|                                 | ciicgat-api-limit                        |
|                                 | ciicgat-api-thirdasset                   |
|                                 | ciicgat-api-assetsES                     |
|                                 | ciicgat-api-assets-io                    |
|                                 | ciicgat-api-coupon                       |
|                                 | ciicgat-api-rights                       |
| 支付中心 ciicgat-agg-payment    | ciicgat-api-payment                      |
|                                 | ciicgat-api-cashier                      |
|                                 | ciicgat-api-excashier                    |
|                                 | ciicgat-api-market                       |
|                                 | ciicgat-api-biz-order                    |
|                                 | ciicgat-api-third-payment                |
|                                 | ciicgat-api-qrcode                       |
|                                 | ~~ciicgat-api-order~~                    |
|                                 | ciicgat-api-pay-authcode                 |
|                                 | ciicgat-api-channel-trans                |
| 结算中心 ciicgat-agg-settlement | ciicgat-api-seller-settlement            |
|                                 | ciicgat-api-enterprise-settlement        |
|                                 | ciicgat-api-invoice                      |
|                                 | ciicgat-api-einvoice                     |
|                                 | ciicgat-api-billing                      |
|                                 | ciicgat-api-nc                           |
|                                 | ciicgat-api-billing-agency               |
|                                 | ciicgat-api-datacenter-report            |
|                                 | ciicgat-api-posting                      |
| 应用产品中心 ciicgat-agg-newapp | ciicgat-api-newapp                       |
|                                 | ciicgat-api-catalog                      |
|                                 | ~~ciicgat-api-php~~                      |
|                                 | ciicgat-api-privilege                    |
|                                 | ciicgat-api-giveapp                      |
|                                 | ciicgat-api-app-display                  |
|                                 | ciicgat-api-announcement                 |
|                                 | ciicgat-api-app-entry                    |
|                                 | ~~ciicgat-api-ticket~~                   |
|                                 | ciicgat-api-prediction-support           |
|                                 | ciicgat-api-value-added                  |
|                                 | ~~ciicgat-api-seller-official-accounts~~ |
|                                 | ciicgat-api-square                       |
|                                 | ciicgat-api-guanaitong-admin             |
|                                 | ciicgat-api-agent                        |
|                                 | ciicgat-api-app-admin                    |
|                                 | ciicgat-api-open-service                 |
| 基础设施 ciicgat-agg-base       | ciicgat-api-common-data                  |
|                                 | ciicgat-api-kms                          |
|                                 | ciicgat-api-ip                           |
|                                 | ciicgat-api-gwf                          |
|                                 | ciicgat-api-notify-agent                 |
|                                 | ciicgat-api-gfs                          |
|                                 | ciicgat-api-notification                 |
|                                 | ciicgat-api-captcha                      |
