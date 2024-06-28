data "aws_iam_policy_document" "bucket_access_policy" {
  statement {
    sid = "S3Read"

    actions = [
      "s3:GetObject"
    ]

    resources = [
      "arn:aws:s3:::<bucket value from secret goes here>/*",
      "arn:aws:s3:::<bucket value from secret goes here>"
    ]
  }
}

resource "aws_iam_role_policy" "bucket_access_policy" {
  name   = "bucket-access-role-policy"
  role   = data.aws_iam_role.ecs_task_role.id
  policy = data.aws_iam_policy_document.bucket_access_policy.json
}
